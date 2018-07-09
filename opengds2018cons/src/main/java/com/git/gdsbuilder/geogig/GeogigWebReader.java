package com.git.gdsbuilder.geogig;

import java.util.List;

import com.git.gdsbuilder.geogig.command.repository.ConfigRepository;
import com.git.gdsbuilder.geogig.command.repository.ListRepository;
import com.git.gdsbuilder.geogig.command.repository.LsTreeRepository;
import com.git.gdsbuilder.geogig.command.repository.PullRepository;
import com.git.gdsbuilder.geogig.command.repository.PushRepository;
import com.git.gdsbuilder.geogig.command.repository.StatusRepository;
import com.git.gdsbuilder.geogig.command.repository.branch.CheckoutBranch;
import com.git.gdsbuilder.geogig.command.repository.branch.CreateBranch;
import com.git.gdsbuilder.geogig.command.repository.branch.ListBranch;
import com.git.gdsbuilder.geogig.command.repository.branch.MergeBranch;
import com.git.gdsbuilder.geogig.command.repository.remote.AddRemoteRepository;
import com.git.gdsbuilder.geogig.command.repository.remote.ListRemoteRepository;
import com.git.gdsbuilder.geogig.command.repository.remote.PingRemoteRepository;
import com.git.gdsbuilder.geogig.command.repository.remote.RemoveRemoteRepository;
import com.git.gdsbuilder.geogig.command.transaction.BeginTransaction;
import com.git.gdsbuilder.geogig.command.transaction.CancleTransaction;
import com.git.gdsbuilder.geogig.command.transaction.EndTransaction;
import com.git.gdsbuilder.geogig.command.workingtree.feature.BlameFeature;
import com.git.gdsbuilder.geogig.type.GeogigBlame;
import com.git.gdsbuilder.geogig.type.GeogigBranch;
import com.git.gdsbuilder.geogig.type.GeogigBranch.Branch;
import com.git.gdsbuilder.geogig.type.GeogigCheckout;
import com.git.gdsbuilder.geogig.type.GeogigConfig;
import com.git.gdsbuilder.geogig.type.GeogigConfig.Config;
import com.git.gdsbuilder.geogig.type.GeogigMerge;
import com.git.gdsbuilder.geogig.type.GeogigPull;
import com.git.gdsbuilder.geogig.type.GeogigPush;
import com.git.gdsbuilder.geogig.type.GeogigRemoteRepository;
import com.git.gdsbuilder.geogig.type.GeogigRepository;
import com.git.gdsbuilder.geogig.type.GeogigRepository.Repo;
import com.git.gdsbuilder.geogig.type.GeogigRevisionTree;
import com.git.gdsbuilder.geogig.type.GeogigRevisionTree.Node;
import com.git.gdsbuilder.geogig.type.GeogigStatus;
import com.git.gdsbuilder.geogig.type.GeogigStatus.Header;
import com.git.gdsbuilder.geogig.type.GeogigTransaction;

public class GeogigWebReader {

	private String baseURL; // "http://localhost:9999/geoserver/geogig"
	private String username; // admin
	private String password; // geoserver

	/**
	 * GeogigWebReader 생성자
	 * 
	 * @param baseurl
	 *            geoserver geogig plugin url
	 * @param username
	 *            geoserver username
	 * @param password
	 *            geoserver password
	 */
	public GeogigWebReader(String baseURL, String username, String password) {
		super();
		this.baseURL = baseURL;
		this.username = username;
		this.password = password;
	}

	/**
	 * begin transaction. GeogigTransaction 반환
	 * 
	 * @param repoName
	 *            Repository 이름
	 * @return GeogigTransaction
	 */
	public GeogigTransaction beginTransaction(String repoName) {

		BeginTransaction begin = new BeginTransaction();
		GeogigTransaction transaction = begin.executeCommand(baseURL, username, password, repoName);
		return transaction;
	}

	/**
	 * end transaction. GeogigTransaction 반환
	 * 
	 * @param repoName
	 *            Repository 이름
	 * @return GeogigTransaction
	 */
	public GeogigTransaction endTransaction(String repoName, String transactionId) {

		EndTransaction end = new EndTransaction();
		GeogigTransaction transaction = end.executeCommand(baseURL, username, password, repoName, transactionId);
		return transaction;
	}

	/**
	 * cancle transaction. GeogigTransaction 반환
	 * 
	 * @param repoName
	 * @param transactionId
	 * @return GeogigTransaction
	 */
	public GeogigTransaction cancleTransaction(String repoName, String transactionId) {

		CancleTransaction end = new CancleTransaction();
		GeogigTransaction transaction = end.executeCommand(baseURL, username, password, repoName, transactionId);
		return transaction;
	}

	/**
	 * Geogig repository 목록, branch 목록, checkout branch working tree, conflict
	 * status 반환
	 * 
	 * @param serverName
	 *            geoserver name
	 * @param repoName
	 *            현재 checkout 된 Repository 이름
	 * @param reference
	 *            현재 checkout 된 Branch 이름
	 * @return GeogigRepositoryTree
	 */
	public GeogigRepositoryTree getWorkingTree(String serverName, String repoName, String reference,
			String transactionId) {

		// json tree
		GeogigRepositoryTree reposTree = new GeogigRepositoryTree();
		reposTree.bulid(serverName);

		// repository
		ListRepository listRepos = new ListRepository();
		GeogigRepository geogigRepo = listRepos.executeCommand(baseURL, username, password);
		List<Repo> repos = geogigRepo.getRepos();

		// branch
		for (Repo repo : repos) {
			String name = repo.getName();
			String storageType = null;

			ConfigRepository configRepos = new ConfigRepository();
			GeogigConfig geogigConfig = configRepos.executeCommand(baseURL, username, password, name, null);
			List<Config> configs = geogigConfig.getConfigs();
			for (Config config : configs) {
				if (config.getName().equals("storage.refs")) {
					storageType = config.getValue();
				}
			}

			reposTree.addRepo(serverName, name, storageType);
			ListBranch listBranch = new ListBranch();
			GeogigBranch branches = listBranch.executeCommand(baseURL, username, password, name);
			List<Branch> localList = branches.getLocalBranchList();
			for (Branch localBranch : localList) {
				String branchName = localBranch.getName();
				if (branchName.equalsIgnoreCase(reference)) {
					StatusRepository stausCommand = new StatusRepository();
					GeogigStatus status = stausCommand.executeCommand(baseURL, username, password, repoName,
							transactionId);
					Header header = status.getHeader();
					String headerBranch = header.getBranch();
					if (branchName.equalsIgnoreCase(headerBranch)) {
						if (status.getUnmerged() != null) {
							reposTree.addBranch(name, branchName, "UnMerged");
						}
						if (status.getUnmerged() != null) {

						} else {
							reposTree.addBranch(name, branchName, "Merged");
						}
					}
				}
			}
		}

		// branch ls-tree : default master
		LsTreeRepository lsTree = new LsTreeRepository();
		GeogigRevisionTree revisionTree = lsTree.executeCommand(baseURL, username, password, repoName, reference);
		List<Node> nodes = revisionTree.getNodes();
		for (Node node : nodes) {
			String path = node.getPath();
			reposTree.add(reference, path);
		}
		return reposTree;
	}

	/**
	 * @param repoName
	 * @param transactionId
	 * @param reference
	 * @return GeogigCheckout
	 */
	public GeogigCheckout checkoutBranch(String repoName, String transactionId, String reference) {

		CheckoutBranch checkout = new CheckoutBranch();
		GeogigCheckout branch = checkout.executeCommand(baseURL, username, password, repoName, transactionId,
				reference);

		return branch;
	}

	/**
	 * @param repoName
	 * @param branchName
	 * @param source
	 * @return GeogigBranch
	 */
	public GeogigBranch createBranch(String repoName, String branchName, String source) {

		CreateBranch create = new CreateBranch();
		GeogigBranch branch = create.executeCommand(baseURL, username, password, repoName, branchName, source);

		return branch;
	}

	/**
	 * @param repoName
	 * @return GeogigBranch
	 */
	public GeogigBranch listBranch(String repoName) {

		ListBranch list = new ListBranch();
		GeogigBranch branch = list.executeCommand(baseURL, username, password, repoName);

		return branch;
	}

	/**
	 * @param repoName
	 * @param transactionId
	 * @param branchName
	 * @return GeogigMerge
	 */
	public GeogigMerge mergeBranch(String repoName, String transactionId, String branchName) {

		MergeBranch merge = new MergeBranch();
		GeogigMerge branch = merge.executeCommand(baseURL, username, password, repoName, transactionId, branchName);

		return branch;
	}

	/**
	 * @param repoName
	 * @param transactionId
	 * @param path
	 * @param version
	 * @return GeogigCheckout
	 */
	public GeogigCheckout checkoutBranch(String repoName, String transactionId, String path, String version) {

		CheckoutBranch checkout = new CheckoutBranch();
		GeogigCheckout branch = checkout.executeCommand(baseURL, username, password, repoName, transactionId, path,
				version);

		return branch;
	}

	/**
	 * @param repoName
	 * @param verbose
	 * @return GeogigRemoteRepository
	 */
	public GeogigRemoteRepository listRemoteRepository(String repoName, boolean verbose) {

		ListRemoteRepository list = new ListRemoteRepository();
		GeogigRemoteRepository remotes = list.executeCommand(baseURL, username, password, repoName, verbose);

		return remotes;
	}

	/**
	 * @param repoName
	 * @param remoteName
	 * @param remoteURL
	 * @return GeogigRemoteRepository
	 */
	public GeogigRemoteRepository addRemoteRepository(String repoName, String remoteName, String remoteURL) {

		AddRemoteRepository add = new AddRemoteRepository();
		GeogigRemoteRepository remotes = add.executeCommand(baseURL, username, password, repoName, remoteName,
				remoteURL);

		return remotes;
	}

	/**
	 * @param repoName
	 * @param remoteName
	 * @param removeTrue
	 * @return GeogigRemoteRepository
	 */
	public GeogigRemoteRepository removeRemoteRepository(String repoName, String remoteName, Boolean removeTrue) {

		RemoveRemoteRepository remove = new RemoveRemoteRepository();
		GeogigRemoteRepository remotes = remove.executeCommand(baseURL, username, password, repoName, remoteName,
				removeTrue);

		return remotes;
	}

	/**
	 * @param repoName
	 * @param remoteName
	 * @return GeogigRemoteRepository
	 */
	public GeogigRemoteRepository pingRemoteRepository(String repoName, String remoteName) {

		PingRemoteRepository ping = new PingRemoteRepository();
		GeogigRemoteRepository remote = ping.executeCommand(baseURL, username, password, repoName, remoteName);

		return remote;
	}

	/**
	 * @param repoName
	 * @param transactionId
	 * @param remoteName
	 * @param branchName
	 * @param remoteBranchName
	 * @param authorName
	 * @param authorEmail
	 * @return GeogigPull
	 */
	public GeogigPull pullRepository(String repoName, String transactionId, String remoteName, String branchName,
			String remoteBranchName, String authorName, String authorEmail) {

		PullRepository pull = new PullRepository();
		GeogigPull geogigPull = pull.executeCommand(baseURL, username, password, repoName, transactionId, remoteName,
				branchName, remoteBranchName, authorName, authorEmail);

		return geogigPull;

	}

	/**
	 * @param repoName
	 * @param remoteName
	 * @param branchName
	 * @param remoteBranchName
	 * @return GeogigPush
	 */
	public GeogigPush pushRepository(String repoName, String remoteName, String branchName, String remoteBranchName) {

		PushRepository push = new PushRepository();
		GeogigPush geogigPush = push.executeCommand(baseURL, username, password, repoName, remoteName, branchName,
				remoteBranchName);

		return geogigPush;
	}

	/**
	 * @param repoName
	 * @param path
	 * @param commit
	 * @return GeogigBlame
	 */
	public GeogigBlame blameFeature(String repoName, String path, String commit) {

		BlameFeature blame = new BlameFeature();
		GeogigBlame geogigBlame = blameFeature(repoName, path, commit);

		return geogigBlame;
	}
}